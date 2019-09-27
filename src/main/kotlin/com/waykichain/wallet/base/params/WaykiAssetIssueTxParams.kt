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

import com.waykichain.wallet.base.*
import com.waykichain.wallet.base.types.encodeInOldWay
import org.bitcoinj.core.*
import org.waykichain.wallet.util.Messages
import org.waykichain.wallet.util.TokenException

/**
 * srcRegId: (regHeight-regIndex)
 * destAddr: 20-byte PubKeyHash
 */
class WaykiAssetIssueTxParams( nValidHeight: Long, fees: Long,val srcRegId: String, feeSymbol: String,val asset:CAsset):
        BaseSignTxParams(feeSymbol,null, null, nValidHeight, fees, WaykiTxType.ASSET_ISSUE_TX, 1) {
    override fun getSignatureHash(): ByteArray {

        val ss = HashWriter()
        ss.add(VarInt(nVersion).encodeInOldWay())
                .add(nTxType.value)
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeRegId(srcRegId)
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
                .addAsset(asset)

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
        val symbolMatch=asset.symbol.matches(SYMBOL_MATCH.toRegex())
        if(!symbolMatch) throw TokenException(Messages.SYMBOLNOTMATCH)
        val sigSize = signature!!.size
        val ss = HashWriter()
        ss.add(VarInt(nTxType.value.toLong()).encodeInOldWay())
                .add(VarInt(nVersion).encodeInOldWay())
                .add(VarInt(nValidHeight).encodeInOldWay())
                .writeRegId(srcRegId)
                .add(feeSymbol)
                .add(VarInt(fees).encodeInOldWay())
                .addAsset(asset)
                .add(VarInt(sigSize.toLong()).encodeInOldWay())
                .add(signature)

        val hexStr = Utils.HEX.encode(ss.toByteArray())
        return hexStr
    }

}
