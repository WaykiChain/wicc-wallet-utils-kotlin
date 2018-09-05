/*
 * Developed by Richard Chen on 9/1/18 12:37 PM
 * Last modified 9/1/18 10:37 AM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet

import com.waykichain.wallet.base.BaseSignTxParams
import org.bitcoinj.core.ECKey

class WaykiCommonTxParams : BaseSignTxParams() {

    override fun signTx(key: ECKey): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSignatureHash(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun serializeTx(): String {
        return ""
    }


    var srcAddr = ""
    var destAddr = ""

    var value = ""
}

