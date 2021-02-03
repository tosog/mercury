using System;
using System.Collections.Generic;
using System.Text;

// Reference the API
using ThingMagic;

namespace PassThroughDemo
{
    /// <summary>
    /// Sample program that demonstrates the passthrough functionality
    /// </summary>
    class Program
    {
        static void Usage()
        {
            Console.WriteLine(String.Join("\r\n", new string[] {
                    " Usage: "+"Please provide valid reader URL, such as: [-v] [reader-uri]",
                    " -v : (Verbose)Turn on transport listener",
                    " reader-uri : e.g., 'tmr:///com4' or 'tmr:///dev/ttyS0/'",
                    " Example for UHF: 'tmr:///com4' or 'tmr:///com4 --ant 1,2' or '-v tmr:///com4 --ant 1,2'",
                    " Example for HF/LF: 'tmr:///com4'"
                }));
            Environment.Exit(1);
        }
        static void Main(string[] args)
        {
            // Program setup
            if (1 > args.Length)
            {
                Usage();
            }

            try
            {
                // Create Reader object, connecting to physical device.
                // Wrap reader in a "using" block to get automatic
                // reader shutdown (using IDisposable interface).
                using (Reader r = Reader.Create(args[0]))
                {
                    //Uncomment this line to add default transport listener.
                    //r.Transport += r.SimpleTransportListener;

                    r.Connect();

                    // Initialize passthrough tag operation with all the required fields
                    UInt32 timeout = 20; // timeout in milliseconds
                    UInt32 configFlags = (UInt32)(ConfigFlags.ENABLE_TX_CRC);
                    byte flags = 0x12;

                    /* Extract random number from response(ICODE Slix-S) */
                    List<byte> buffer = new List<byte>(); //buffer
                    buffer.Add(flags); // flags
                    buffer.Add(0xB2); // GET_RANDOM_NUMBER
                    buffer.Add(0x04); //IC_MFG_CODE_NXP

                    PassThrough passThroughOp = new PassThrough(timeout, configFlags, buffer);
                    byte[] passThroughResp = (byte[])r.ExecuteTagOp(passThroughOp, null);

                }
            }
            catch (ReaderException re)
            {
                Console.WriteLine("Error: " + re.Message);
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: " + ex.Message);
            }
        }
    }
}