package org.waykichain.wallet.util

import com.google.common.collect.ImmutableList
import org.bitcoinj.crypto.ChildNumber
import java.util.*


class BIP44Util{
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
    }




}