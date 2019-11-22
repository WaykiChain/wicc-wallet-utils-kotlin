/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 The Waykichain Core developers
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
 */

package com.waykichain.wallet.base.params

import com.waykichain.wallet.base.CoinType
import com.waykichain.wallet.base.HashWriter
import com.waykichain.wallet.base.WaykiNetworkType
import com.waykichain.wallet.base.WaykiTxType
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.*

/**
 * srcRegId: (regHeight-regIndex)
 * destAddr: 20-byte PubKeyHash
 */
class WaykiCommonTxParams(networkType: WaykiNetworkType, nValidHeight: Long,pubKey:String, fees: Long, val value: Long, val srcRegId: String, destAddr: String,val memo: String):
        BaseSignTxParams(CoinType.WICC.type,pubKey, null, nValidHeight, fees, WaykiTxType.TX_COMMON, 1) {
    val netParams = if (networkType == WaykiNetworkType.MAIN_NET) WaykiMainNetParams.instance else WaykiTestNetParams.instance
    val legacyAddress = LegacyAddress.fromBase58(netParams, destAddr)

    override fun getSignatureHash(): ByteArray {
        val publicKey= Utils.HEX.decode(userPubKey)
        val ss = HashWriter()
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(srcRegId,publicKey)
                .add(VarInt(legacyAddress.hash.size.toLong()).encodeInOldWay())
                .add(legacyAddress.hash)
                .add(VarInt(fees).encodeInOldWay())
                .add(VarInt(value).encodeInOldWay())
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
        val publicKey=Utils.HEX.decode(userPubKey)
        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeUserId(srcRegId,publicKey)
                .add(VarInt(legacyAddress.hash.size.toLong()).encodeInOldWay())
                .add(legacyAddress.hash)
                .add(VarInt(fees).encodeInOldWay())
                .add(VarInt(value).encodeInOldWay())
                .add(memo)
                .writeCompactSize(sigSize.toLong())
                .add(signature)

        val hexStr = Utils.HEX.encode(ss.toByteArray())
        return hexStr
    }

}
