package com.waykichain.wallet.base.params

import com.google.common.base.Strings
import com.waykichain.wallet.base.HashReader
import com.waykichain.wallet.base.HashWriter
import com.waykichain.wallet.base.UCoinDest
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.*

/**
 * srcRegId: (regHeight-regIndex PubKeyHash)
 * destAddr: 20-byte PubKeyHash
 * fee Minimum 0.001 wicc
 */
class WaykiUCoinTxParams( nValidHeight: Long, val userId: String, userPubKey: String,
                         val dests:List<UCoinDest>, feeSymbol: String, fees: Long, val memo: String) :
        BaseSignTxParams(feeSymbol, userPubKey, null, nValidHeight, fees, WaykiTxType.TX_UCOIN_TRANSFER, 1) {

    override fun getSignatureHash(): ByteArray {
        val ss = HashWriter()
        val pubKey = Utils.HEX.decode(userPubKey)
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId, pubKey)
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
                .addUCoinDestAddr(dests)
                .add(memo)

        val hash = Sha256Hash.hashTwice(ss.toByteArray())
        val hashStr = Utils.HEX.encode(hash)
        System.out.println("hash: $hashStr")

        return hash
    }

    override fun signTx(key: ECKey): ByteArray {
        val sigHash = this.getSignatureHash()
        val ecSig = key.sign(Sha256Hash.wrap(sigHash))
        signature = ecSig.encodeToDER()
        return signature!!
    }

    override fun serializeTx(): String {
        assert(signature != null)
        val sigSize = signature!!.size
        val pubKey = Utils.HEX.decode(userPubKey)
        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId, pubKey)
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
                .addUCoinDestAddr(dests)
                .add(memo)
                .add(VarInt(sigSize.toLong()).encodeInOldWay())
                .add(signature)

        val hexStr = Utils.HEX.encode(ss.toByteArray())
        return hexStr
    }

    companion object {
        fun unSerializeTx(ss: HashReader, params: NetworkParameters): BaseSignTxParams {
            //val ss = HashReader(Utils.HEX.decode(param))
            //val nTxType = WaykiTxType.init(ss.readVarInt().value.toInt())
            val nVersion = ss.readVarInt().value
            val nValidHeight = ss.readVarInt().value
            val array = ss.readUserId()
            val userId = array[0]
            val pubKey = array[1]
            val feeSymbol = ss.readString()
            val fees = ss.readVarInt().value
            val dests = ArrayList<UCoinDest>()
            ss.readUCoinDestAddr(dests, params)
            val memo = ss.readString()
            val signature = ss.readByteArray()
            val ret =  WaykiUCoinTxParams(nValidHeight, userId, pubKey, dests, feeSymbol, fees, memo)
            //ret.nTxType = nTxType
            ret.nVersion = nVersion
            ret.signature = signature
            return ret
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("[nTxType]=").append(nTxType).append("\n")
                .append("[nVersion]=").append(nVersion).append("\n")
                .append("[nValidHeight]=").append(nValidHeight).append("\n")
                .append("[userId]=").append(if(Strings.isNullOrEmpty(userId)) userPubKey else userId).append("\n")
                //.append("[pubKey]=").append(userPubKey).append("\n")
                .append("[feeSymbol]=").append(feeSymbol).append("\n")
                .append("[fees]=").append(fees).append("\n")
                .append("[memo]=").append(memo).append("\n")
                .append("[signature]=").append(Utils.HEX.encode(signature)).append("\n")
        for(dest in dests) {
            builder.append("[destAddress]=").append(dest.destAddress).append("\n")
                    .append("[coinSymbol]=").append(dest.coinSymbol).append("\n")
                    .append("[transferAmount]=").append(dest.transferAmount).append("\n")
        }
        return builder.toString()
    }



}
