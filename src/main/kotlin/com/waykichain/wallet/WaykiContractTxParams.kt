/*
 * Developed by Richard Chen on 9/1/18 12:37 PM
 * Last modified 9/1/18 10:32 AM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet

import com.waykichain.wallet.base.BaseSignTxParams
import com.waykichain.wallet.base.types.unsigned.Uint8
import org.bitcoinj.core.ECKey

class WaykiContractTxParams: BaseSignTxParams() {

    override fun getSignatureHash(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signTx(key: ECKey): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun serializeTx(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var pubKey = ""
    var minerPubKey = ""
    private var contract = ArrayList<Uint8>()

}