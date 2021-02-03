/**
 * Sample program that demonstrate the passThrough functionality
 * @file passThrough.c
 */

#include <tm_reader.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

/* Enable this to use transportListener */
#ifndef USE_TRANSPORT_LISTENER
#define USE_TRANSPORT_LISTENER 0
#endif

#define OPCODE_GET_RANDOM_NUMBER 0xB2
#define  IC_MFG_CODE_NXP 0x04
#define MAX_RESPONSE_LENGTH 240

#define usage() {errx(1, "Please provide valid reader URL, such as: reader-uri\n"\
                         "reader-uri : e.g., 'tmr:///COM1' or 'tmr:///dev/ttyS0/' or 'tmr://readerIP'\n"\
                         "Example: 'tmr:///com4'\n");}

void errx(int exitval, const char *fmt, ...)
{
  va_list ap;

  va_start(ap, fmt);
  vfprintf(stderr, fmt, ap);

  exit(exitval);
}

void checkerr(TMR_Reader* rp, TMR_Status ret, int exitval, const char *msg)
{
  if (TMR_SUCCESS != ret)
  {
    errx(exitval, "Error %s: %s\n", msg, TMR_strerr(rp, ret));
  }
}

void serialPrinter(bool tx, uint32_t dataLen, const uint8_t data[],
                   uint32_t timeout, void *cookie)
{
  FILE *out = cookie;
  uint32_t i;

  fprintf(out, "%s", tx ? "Sending: " : "Received:");
  for (i = 0; i < dataLen; i++)
  {
    if (i > 0 && (i & 15) == 0)
    {
      fprintf(out, "\n         ");
    }
    fprintf(out, " %02x", data[i]);
  }
  fprintf(out, "\n");
}

void stringPrinter(bool tx,uint32_t dataLen, const uint8_t data[],uint32_t timeout, void *cookie)
{
  FILE *out = cookie;

  fprintf(out, "%s", tx ? "Sending: " : "Received:");
  fprintf(out, "%s\n", data);
}

int main(int argc, char *argv[])
{
  TMR_Reader r, *rp;
  TMR_Status ret;
#ifdef TMR_ENABLE_HF_LF
  uint32_t timeout;
  TMR_uint8List cmd;
  TMR_uint8List response;
  TMR_TagOp passThroughOp;
  uint8_t flags, responseData[MAX_RESPONSE_LENGTH];
  TMR_Reader_configFlags configFlags;
  uint8_t cmdStr[TMR_SR_MAX_PACKET_SIZE];
#endif /* TMR_ENABLE_HF_LF */

#if USE_TRANSPORT_LISTENER
  TMR_TransportListenerBlock tb;
#endif
 
  if (argc < 2)
  {
    usage();
  }
  
  rp = &r;
  ret = TMR_create(rp, argv[1]);
  checkerr(rp, ret, 1, "creating reader");

#if USE_TRANSPORT_LISTENER

  if (TMR_READER_TYPE_SERIAL == rp->readerType)
  {
    tb.listener = serialPrinter;
  }
  else
  {
    tb.listener = stringPrinter;
  }
  tb.cookie = stdout;

  TMR_addTransportListener(rp, &tb);
#endif

  ret = TMR_connect(rp);
  checkerr(rp, ret, 1, "connecting reader");

#ifdef TMR_ENABLE_HF_LF
  response.list = responseData;
  response.max = sizeof(responseData) / sizeof(responseData[0]);
  response.len = 0;

  cmd.len = cmd.max = 0;
  cmd.list = cmdStr;

  timeout = 20; //timeout in milliseconds
  flags = 0x12;
  configFlags = TMR_READER_CONFIG_FLAGS_ENABLE_TX_CRC | TMR_READER_CONFIG_FLAGS_ENABLE_RX_CRC |
                TMR_READER_CONFIG_FLAGS_ENABLE_INVENTORY;

  /* Extract random number from response(ICODE Slix-S) */
  cmd.list[cmd.len++] = flags;
  cmd.list[cmd.len++] = OPCODE_GET_RANDOM_NUMBER;
  cmd.list[cmd.len++] = IC_MFG_CODE_NXP;

  ret = TMR_TagOp_init_PassThrough(&passThroughOp, timeout, configFlags, &cmd);
  checkerr(rp, ret, 1, "Creating passthrough tagop to get RN");

  ret = TMR_executeTagOp(rp, &passThroughOp, NULL, &response);
  checkerr(rp, ret, 1, "Executing passthrough tagop to get RN");
#endif /* TMR_ENABLE_HF_LF */

  TMR_destroy(rp);
  return 0;
}
