/*  @file Lf125Khz.java
 *  @brief Mercury API - Lf125Khz tag information and interfaces
 *  @author pchinnapapannagari
 *  @date 3/30/2020

 * Copyright (c) 2020 Jadak, a business unit of Novanta Corporation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.thingmagic;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public class Lf125khz 
{
    public enum TagType
    {
        // Auto detect - supports all tag types
        AUTO_DETECT(0x00000001),
        // AWID Tagtype
        AWID(0x01000000),
        // HID PROX II tag type
        HID_PROX(0x02000000),
        // NXP HITAG 1 tag type
        HITAG_1(0x04000000),
        // NXP HITAG 2 tag type
        HITAG_2(0x08000000),
        // EM4100 tag type
        EM_4100(0x10000000),
        //ALL
        ALL(0x1F000001);
        int rep;

        TagType(int rep) {
            this.rep = rep;
        }
        private static final Map<Integer, TagType> lookup = new HashMap<Integer, TagType>();

        static 
        {
            for (TagType type : EnumSet.allOf(TagType.class)) {
                lookup.put(type.getCode(), type);
            }
        }

        public int getCode() {
            return rep;
        }

        public static TagType get(int rep) {
            return lookup.get(rep);
        }
    }
    
    public static class TagData extends com.thingmagic.TagData
    {
        // Tag's RFID protocol
        @Override
        public  TagProtocol getProtocol()
        {
                return TagProtocol.LF125KHZ;
        }
        // Create TagData with blank CRC
        // <param name="uidBytes">UID value</param>
        public TagData(byte[] uidBytes) { super(uidBytes); }
        
    }
}
