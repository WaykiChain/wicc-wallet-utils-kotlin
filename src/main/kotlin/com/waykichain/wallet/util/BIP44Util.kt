package org.waykichain.wallet.util

import com.google.common.collect.ImmutableList
import com.waykichain.wallet.base.params.WaykiTestNetParams
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.LegacyAddress
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import java.util.*


class BIP44Util {
    companion object {
        val WAYKICHAIN_WALLET_PATH = "m/44'/99999'/0'"

        fun generatePath(path: String): ImmutableList<ChildNumber> {
            val list = ArrayList<ChildNumber>()
            for (p in path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                if ("m".equals(p, ignoreCase = true) || "" == p.trim { it <= ' ' }) {
                    continue
                } else if (p[p.length - 1] == '\'') {
                    list.add(ChildNumber(Integer.parseInt(p.substring(0, p.length - 1)), true))
                } else {
                    list.add(ChildNumber(Integer.parseInt(p), false))
                }
            }
            val builder = ImmutableList.builder<ChildNumber>()
            return builder.addAll(list).build()
        }

        fun generateWaykiWallet(wordStr: String, networkParameters: NetworkParameters): WaykiWallet {
            val words = wordStr.split(" ")
            MnemonicUtil.validateMnemonics(words)
            val seed = DeterministicSeed(words, null, "", 0L)
            val keyChain = DeterministicKeyChain.builder().seed(seed).build()
            val mainKey = keyChain.getKeyByPath(BIP44Util.generatePath(BIP44Util.WAYKICHAIN_WALLET_PATH + "/0/0"), true)
            val address = LegacyAddress.fromPubKeyHash(networkParameters, mainKey.pubKeyHash).toString()
            val ecKey = ECKey.fromPrivate(mainKey.privKey)
            val privateKey=ecKey.getPrivateKeyAsWiF(networkParameters)
            val pubKey=ecKey.publicKeyAsHex
            val waykiWallet=WaykiWallet(privateKey,pubKey,address)
            return waykiWallet
        }
    }

    data class WaykiWallet(var privateKey: String, var pubKey: String, var address: String)

}