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

package com.waykichain.wallet

import com.waykichain.wallet.base.WalletAddress
import com.waykichain.wallet.base.WaykiNetworkType
import com.waykichain.wallet.base.params.WaykiCommonTxParams
import com.waykichain.wallet.base.params.WaykiContractTxParams
import com.waykichain.wallet.base.params.WaykiDelegateTxParams
import com.waykichain.wallet.base.params.WaykiRegisterAccountTxParams

interface LegacyWalletInterface {

    /**
     * creation of wallet priv/pub key pair and corresponding address
     */
    fun generateWalletAddress(networkType: WaykiNetworkType): WalletAddress

    /**
     * offline creation of Register Account Transaction raw data
     */
    fun createRegisterTransactionRaw(params: WaykiRegisterAccountTxParams): String

    /**
     * offline creation of Common Transaction raw data
     */
    fun createCommonTransactionRaw(params: WaykiCommonTxParams): String

    /**
     * offline creation of Contract Transaction raw data
     */
    fun createContractTransactionRaw(params: WaykiContractTxParams): String

    /**
     * offline creation of Delegate Transaction raw data
     */
    fun createDelegateTransactionRaw(params: WaykiDelegateTxParams): String

}