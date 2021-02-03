/*
 * Sample program to demonstrate the functionality of passthrough 
 */
package samples;

import com.thingmagic.Reader;
import com.thingmagic.TransportListener;
import java.util.ArrayList;
import java.util.List;
import com.thingmagic.PassThrough;

/**
 *
 * @author bchanda
 */
public class PassThroughDemo
{
    static void usage()
    {
        System.out.printf("Usage: Please provide valid arguments, such as:\n"
                + "[-v] [reader-uri] \n" +
                  "-v  Verbose: Turn on transport listener\n" +
                  "reader-uri  Reader URI: e.g., \"tmr:///COM1\"\n"
                + "e.g: tmr:///com1  ; tmr:///dev/ttyS0/\n ");
        System.exit(1);
    }

    public static void setTrace(Reader r, String args[])
    {
        if (args[0].toLowerCase().equals("on"))
        {
            r.addTransportListener(r.simpleTransportListener);
        }
    }

    static class StringPrinter implements TransportListener
    {
        public void message(boolean tx, byte[] data, int timeout)
        {
            System.out.println((tx ? "Sending:\n" : "Receiving:\n")
                    + new String(data));
        }
    }

    static class SerialPrinter implements TransportListener
    {
        public void message(boolean tx, byte[] data, int timeout)
        {
            System.out.print(tx ? "Sending: " : "Received:");
            for (int i = 0; i < data.length; i++)
            {
                if (i > 0 && (i & 15) == 0) {
                    System.out.printf("\n");
                }
                System.out.printf(" %02x", data[i]);
            }
            System.out.printf("\n");
        }
    }

    public static void main(String argv[])
    {
        Reader r = null;
        int nextarg = 0;
        boolean trace = false;

        if (argv.length < 1)
        {
            usage();
        }

        if (argv[nextarg].equals("-v"))
        {
            trace = true;
            nextarg++;
        }

        try
        {
            r = Reader.create(argv[nextarg]);
            if (trace)
            {
                setTrace(r, new String[]{"on"});
            }
            r.connect();
            
            // Initialize passthrough tag operation with all the required fields
            int timeout = 20; // timeout in milliseconds
            int configFlags = PassThrough.ConfigFlags.ENABLE_TX_CRC.getCode();
            byte flags = 0x12;

            /* Extract random number from response(ICODE Slix-S) */
            List<Byte> buffer = new ArrayList<Byte>(); //buffer
            buffer.add(flags); // flags
            buffer.add((byte)0xB2); // GET_RANDOM_NUMBER
            buffer.add((byte)0x04); //IC_MFG_CODE_NXP

            PassThrough passThroughOp = new PassThrough(timeout, configFlags, buffer);
            byte[] passThroughResp = (byte[])r.executeTagOp(passThroughOp, null);

            // Shut down reader
            r.destroy();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
