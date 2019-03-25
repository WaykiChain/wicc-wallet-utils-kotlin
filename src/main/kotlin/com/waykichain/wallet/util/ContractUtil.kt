package com.waykichain.wallet.util

class ContractUtil{

    companion object {

    fun to2HexString4byte(value: Long?): String {
        var s = java.lang.Long.toHexString(value!!)
        var first = ""
        if (s.length % 2 == 1) {
            first = "0" + s.substring(0, 1)
            s = s.substring(1, s.length)
        }
        val sb = StringBuilder()
        var i = s.length
        while (i > 1) {
            sb.append(s.substring(i - 2, i))
            i = i - 2
        }
        sb.append(first)
        return fillZero(sb.toString(), 8)
    }

    fun fillZero(str: String, maxlength: Int): String {
        val fillLenth = maxlength - str.length
        val sb = StringBuilder(str)
        for (i in 0 until fillLenth) {
            sb.append("0")
        }
        return sb.toString()
    }

    /**
     * 16进制转二进制
     */
    fun hexString2binaryString(hexStringIn: String): ByteArray? {
        if ( hexStringIn == "")
            return null

        val hexString = hexStringIn.toUpperCase()
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)

        for (i in 0 until length) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }

        return d
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
     fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }
    }


}