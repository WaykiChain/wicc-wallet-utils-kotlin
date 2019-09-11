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

package com.waykichain.wallet.base

enum class WaykiTxType(val value: Int) {
    TX_NONE(0),
    TX_REGISTERACCOUNT(2),
    TX_COMMON(3),
    TX_CONTRACT(4),
    LCONTRACT_DEPLOY_TX(5),
    TX_DELEGATE(6),

    ASSET_ISSUE_TX(9),    //!< a user issues onchain asset
    ASSET_UPDATE_TX(10),   //!< a user update onchain asset

    TX_UCOIN_TRANSFER(11),
    UCONTRACT_DEPLOY_TX(14),   //!< universal VM contract deployment
    UCONTRACT_INVOKE_TX(15),   //!< universal VM contract invocation

    TX_CDPSTAKE(21),
    TX_CDPREDEEM(22),
    TX_CDPLIQUIDATE(23),

    DEX_CANCEL_ORDER_TX(88),//!< dex cancel order Tx
    DEX_BUY_LIMIT_ORDER_TX(84), //!< dex buy limit price order Tx
    DEX_SELL_LIMIT_ORDER_TX(85),//!< dex sell limit price order Tx
    DEX_BUY_MARKET_ORDER_TX(86),//!< dex buy market price order Tx
    DEX_SELL_MARKET_ORDER_TX(87), //!< dex sell market price order Tx
}

enum class CoinType(val type: String) {
    WICC("WICC"),
    WUSD("WUSD"),
    WGRT("WICC"),
    WCNY("WCNY"),
    WBTC("WBTC"),
    WETH("WETH"),
    WEOS("WEOS"),
    USD("USD"),
    CNY("CNY"),
    EUR("EUR"),
    BTC("BTC"),
    USDT("USDT"),
    GOLD("GOLD"),
    KWH("KWH")
}

enum class VoteOperType(val value: Int) {
    NULL_OPER(0),            //
    ADD_FUND(1),        //投票
    MINUS_FUND(2),    //撤销投票
}

enum class AssetUpdateType(val type: Int) {
    UPDATE_NONE(0),
    OWNER_UID(1),
    NAME(2),
    MINT_AMOUNT(3)
}

data class AssetUpdateData(var enumAsset: AssetUpdateType, var value: Any)

