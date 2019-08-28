package com.waykichain.wallet.base.params

import com.waykichain.wallet.base.HashWriter
import com.waykichain.wallet.base.WaykiNetworkType
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.cdpHash
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.*

/**
 * srcRegId: (regHeight-regIndex PubKeyHash)
 * destAddr: 20-byte PubKeyHash
 * fee Minimum 0.001 wicc
 */
class WaykiUCoinTxParams(networkType: WaykiNetworkType, nValidHeight: Long, val userId: String, userPubKey: String,
                         val toUserId: String,val coinSymbol: String,val coinAmount: Long,feeSymbol: String,fees: Long,val memo: String) :
        BaseSignTxParams(feeSymbol, userPubKey, null, nValidHeight, fees, WaykiTxType.TX_UCOIN_TRANSFER, 1) {
    val netParams = if (networkType == WaykiNetworkType.MAIN_NET) WaykiMainNetParams.instance else WaykiTestNetParams.instance
    val legacyAddress = LegacyAddress.fromBase58(netParams, toUserId)
    override fun getSignatureHash(): ByteArray {
        val ss = HashWriter()
        val pubKey = Utils.HEX.decode(userPubKey).reversedArray()
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId, pubKey)
                .add(VarInt(legacyAddress.hash.size.toLong()).encodeInOldWay())
                .add(legacyAddress.hash)
                .add(coinSymbol)
                .add(VarInt(coinAmount).encodeInOldWay())
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
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
        val pubKey = Utils.HEX.decode(userPubKey).reversedArray()
        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(userId, pubKey)
                .add(VarInt(legacyAddress.hash.size.toLong()).encodeInOldWay())
                .add(legacyAddress.hash)
                .add(coinSymbol)
                .add(VarInt(coinAmount).encodeInOldWay())
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
                .add(memo)
                .add(VarInt(sigSize.toLong()).encodeInOldWay())
                .add(signature)

        val hexStr = Utils.HEX.encode(ss.toByteArray())
        return hexStr
    }
}
