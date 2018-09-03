/*
 * Developed by Richard Chen on 8/31/18 7:47 PM
 * Last modified 8/31/18 7:47 PM
 * Copyright (c) 2018. All rights reserved.
 *
 */

package com.waykichain.wallet

enum class WaykiNetworkType(val type: Int, val title: String) {
    MAIN_NET    (1, "mainnet"),
    TEST_NET    (2, "testnet"),
    REGTEST_NET (3, "regtest")
}