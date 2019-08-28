package com.waykichain.wallet.base.params

import com.waykichain.wallet.base.HashWriter
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.cdpHash
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils
import org.bitcoinj.core.VarInt

/**
 * srcRegId: (regHeight-regIndex PubKeyHash)
 * destAddr: 20-byte PubKeyHash
 * fee Minimum 0.0001 wicc
 */
class WaykiDexLimitTxParams(nValidHeight: Long, fees: Long,val userId: String, userPubKey: String?, feeSymbol: String,
                            val coinSymbol: String,val assetSymbol: String,val assetAmount: Long,val price:Long,
                            txType:WaykiTxType) :
        BaseSignTxParams(feeSymbol, userPubKey, null, nValidHeight, fees, txType, 1) {

    override fun getSignatureHash(): ByteArray {
        val ss = HashWriter()
        val pubKey = Utils.HEX.decode(userPubKey).reversedArray()
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId, pubKey)
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
                .add(coinSymbol)
                .add(assetSymbol)
                .add(VarInt(assetAmount).encodeInOldWay())
                .add(VarInt(price).encodeInOldWay())
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
        val pubKey = Utils.HEX.decode(userPubKey).reversedArray()
        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId, pubKey)
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
                .add(coinSymbol)
                .add(assetSymbol)
                .add(VarInt(assetAmount).encodeInOldWay())
                .add(VarInt(price).encodeInOldWay())
                .add(VarInt(sigSize.toLong()).encodeInOldWay())
                .add(signature)

        val hexStr = Utils.HEX.encode(ss.toByteArray())
        return hexStr
    }
}
