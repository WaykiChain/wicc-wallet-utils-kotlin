/*
 * Developed by Richard Chen on 9/1/18 12:37 PM
 * Last modified 9/1/18 12:35 PM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet

import com.waykichain.wallet.base.BaseSignTxParams
import org.bitcoin.NativeSecp256k1
import org.bitcoinj.core.ECKey
import java.io.ByteArrayOutputStream
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Utils

class WaykiRegisterAccountTxParams: BaseSignTxParams() {

    init {
        nTxType = 2
    }

    override fun getSignatureHash(): ByteArray {

        val ss = ByteArrayOutputStream()
        ss.write(nVersion)
        ss.write(nTxType)
        ss.write(nValidHeight)
        ss.write(pubKey.toByteArray())
        ss.write(minerPubKey.toByteArray())
        ss.write(fees)

        val hash = Sha256Hash.hashTwice(ss.toByteArray())
        return hash
    }

    override fun signTx(key: ECKey): ByteArray {

        val sigHash = this.getSignatureHash()
        signature = NativeSecp256k1.sign(sigHash, key.privKeyBytes)
        return signature!!
    }

    override fun serializeTx(): String {
//        if (signature == null)
//            signTx(this.)

        val ss = ByteArrayOutputStream()
        ss.write(nTxType)
        ss.write(nVersion)
        ss.write(nValidHeight)
        ss.write(pubKey.toByteArray())
        ss.write(minerPubKey.toByteArray())
        ss.write(fees)
        ss.write(signature)

        val hexStr =  Utils.HEX.encode(ss.toByteArray())
        return hexStr

    }

    var pubKey = ""
    var minerPubKey = ""

}