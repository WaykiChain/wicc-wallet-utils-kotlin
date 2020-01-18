package com.waykichain.wallet

import com.waykichain.wallet.base.OperVoteFund
import com.waykichain.wallet.base.VoteOperType
import com.waykichain.wallet.base.params.WaykiDelegateTxParams
import com.waykichain.wallet.impl.LegacyWallet
import org.bitcoinj.core.ECKey
import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import org.junit.Test
import org.slf4j.LoggerFactory
import org.waykichain.wallet.util.BIP44Util
import org.waykichain.wallet.util.MnemonicUtil
import java.util.*

class RunWallet {
    private val logger = LoggerFactory.getLogger(javaClass)


    @Test
    fun runGenerateVoteDelegateTx() {
        val wallet = LegacyWallet()
        val coinUnit = 100000000L
        val srcRegId = "2235518-1"
        val fees = 10000L

        val voteAmount = 248000 * coinUnit
        val validHeight = 2385353L


        val delegatePubKeyList = arrayOf(
                "02d5b0c28802250ff0e48ba1961b79337b1ed4c2a7e695f5b0b41c6777b1bd2bcf",   //0-2
                "02e829790a0bcfc5b62547a38ef2880dd653df61d52d1523d22e0d5431fc0bae3b",   //0-3
                "03145134d18bbcb1da64adb201d1234f57b3daee8bb7d0bcbe7c27b53edadcab59"    //0-4
                )


        val srcKey = getMainnetEcKeyFromMnecode()

        val voteArr = arrayListOf<OperVoteFund>()
        for ( pubKeyHex in delegatePubKeyList) {
            val pubKey = decodeHexString(pubKeyHex)
            val vote = OperVoteFund(VoteOperType.ADD_FUND.value, pubKey, voteAmount)
            voteArr.add(vote)
        }

        val txParams = WaykiDelegateTxParams(srcRegId,srcKey.publicKeyAsHex, voteArr.toTypedArray(), fees, validHeight)
        txParams.signTx(srcKey)
        val tx = wallet.createDelegateTransactionRaw(txParams)

        logger.info("Raw Tx String: \n$tx")
    }

    private fun getMainnetEcKeyFromMnecode(): ECKey {
        System.out.println("Please enter 12-word mnemonic phrase below: \n")
        val words = Scanner(System.`in`).nextLine()
        System.out.println("You just entered: {$words}")

        val wordList = if (words.contains(",")) words.split(", ") else words.split(" ")
        MnemonicUtil.validateMnemonics(wordList)

        val seed = DeterministicSeed(wordList, null, "", 0L)
        val keyChain = DeterministicKeyChain.builder().seed(seed).build()
        val mainKey = keyChain.getKeyByPath(BIP44Util.generatePath(BIP44Util.WAYKICHAIN_WALLET_PATH + "/0/0"), true)
        return ECKey.fromPrivate(mainKey.privKey)
    }

    fun decodeHexString(hexString: String): ByteArray {
        if (hexString.length % 2 == 1)
            throw IllegalArgumentException("Invalid hexadecimal String supplied.")

        val bytes = ByteArray(hexString.length / 2)
        var i = 0
        while (i < hexString.length) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2))
            i += 2
        }
        return bytes
    }

    fun hexToByte(hexString: String): Byte {
        val firstDigit = toDigit(hexString[0])
        val secondDigit = toDigit(hexString[1])
        return ((firstDigit shl 4) + secondDigit).toByte()
    }

    private fun toDigit(hexChar: Char): Int {
        val digit = Character.digit(hexChar, 16)
        if (digit == -1) {
            throw IllegalArgumentException(
                    "Invalid Hexadecimal Character: $hexChar")
        }
        return digit
    }
}