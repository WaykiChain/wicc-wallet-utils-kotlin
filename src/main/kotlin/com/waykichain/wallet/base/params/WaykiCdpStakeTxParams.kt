package com.waykichain.wallet.base.params

import com.waykichain.wallet.base.HashWriter
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.*

/**
 * srcRegId: (regHeight-regIndex)
 * destAddr: 20-byte PubKeyHash
 */
class WaykiCdpStakeTxParams(nValidHeight: Long, fees: Long,
                            val userId: String, userPubKey:ByteArray, val cdpTxid:String,
                            feeSymbol:String,val bCoinSymbol:String, val sCoinSymbol:String,
                            val bCoinToStake:Long,val sCoinToMint:Long):
        BaseSignTxParams(feeSymbol, userPubKey ,null, nValidHeight, fees, WaykiTxType.TX_CDPSTAKE, 1) {
    val cdpTxHexArr=Utils.HEX.decode(cdpTxid).reversedArray()
    override fun getSignatureHash(): ByteArray {
        val ss = HashWriter()
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId,userPubKey)
                .add(feeSymbol.toByteArray())
                .add(VarInt(fees).encodeInOldWay())
                .add(cdpTxHexArr)
                .add(bCoinSymbol.toByteArray())
                .add(sCoinSymbol.toByteArray())
                .add(VarInt(bCoinToStake).encodeInOldWay())
                .add(VarInt(sCoinToMint).encodeInOldWay())
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

        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId,userPubKey)
                .add(feeSymbol.toByteArray())
                .add(VarInt(fees).encodeInOldWay())
                .add(cdpTxHexArr)
                .add(bCoinSymbol.toByteArray())
                .add(sCoinSymbol.toByteArray())
                .add(VarInt(bCoinToStake).encodeInOldWay())
                .add(VarInt(sCoinToMint).encodeInOldWay())
                .add(VarInt(sigSize.toLong()).encodeInOldWay())
                .add(signature)

        val hexStr = Utils.HEX.encode(ss.toByteArray())
        return hexStr
    }
}
