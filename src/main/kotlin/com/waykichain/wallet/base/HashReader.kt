package com.waykichain.wallet.base

import com.waykichain.wallet.base.types.decode
import com.waykichain.wallet.base.types.size
import org.bitcoinj.core.LegacyAddress
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.Utils
import org.bitcoinj.core.VarInt
import java.io.ByteArrayInputStream

class HashReader(buf: ByteArray) : ByteArrayInputStream(buf) {

    fun readString():String{
        val len = this.readCompactSize().toInt()//VarInt(0).decode(this)
        val array = ByteArray(len)//len.value.toInt())
        this.read(array,0, len)//.value.toInt())
        return String(array)
    }

    fun readVarInt():VarInt {
        return VarInt(0).decode(this)
    }

    fun readCompactSize():Long{
        var cSize=0L
        val chSize=this.read()
        if (chSize < 253){
            cSize = chSize.toLong()
        }else if (chSize == 253){
            var  xSize:Short
            xSize=this.read().toShort()
            cSize = xSize.toLong()
            if (cSize < 253)
                throw Exception("non-canonical ReadCompactSize()");
        }else if (chSize == 254)
        {
            var xSize:Int
            xSize=this.read()
            cSize = xSize.toLong()
            if (cSize < 0x10000)
                throw Exception("non-canonical ReadCompactSize()");
        }else{
            var xSize:Long
            xSize=this.read().toLong()
            cSize = xSize.toLong()
            if (cSize < 0x100000000L)
                throw Exception("non-canonical ReadCompactSize()")
        }
        if (cSize > Long.MAX_VALUE)
            throw Exception("ReadCompactSize() : size too large")
        return cSize
    }

    fun readRegId(): String {
        val regIdLen = this.readVarInt()
        val height = this.readVarInt().value
        val index = this.readVarInt().value
//        val regId:WaykiRegId = WaykiRegId(height, index)
        return height.toString() + "-" + index.toString()
    }

    fun readPubKey():String {
        this.mark(this.pos)
        val keySize = this.read()
        if(keySize!=33){
            this.reset()
            print(this.pos)
            return ""
        }
        val array = ByteArray(keySize)
        this.read(array,0, keySize)
        return Utils.HEX.encode(array)
    }

    fun readUserId(): Array<String> {
        var regId =""
        val pubKey = this.readPubKey()
        if(pubKey=="")regId = this.readRegId()
        return arrayOf(regId, pubKey)
    }

    fun readUCoinDestAddr(dests: ArrayList<UCoinDest>, params: NetworkParameters) {
        val size = this.readVarInt().value
        dests.clear()
        for(a in 1..size) {
            val len = this.readVarInt()
            val array = ByteArray(len.value.toInt())
            this.read(array,0, len.value.toInt())
            val addr  = LegacyAddress.fromPubKeyHash(params, array)
            val coinSymbol = this.readString()
            val transferAmount = this.readVarInt().value
            dests.add(UCoinDest(addr, coinSymbol, transferAmount))
        }
    }

    fun readByteArray() :ByteArray{
        val len = this.readCompactSize().toInt()//readVarInt()
        val array = ByteArray(len)//.value.toInt())
        this.read(array,0, len)//.value.toInt())
        return array
    }
}