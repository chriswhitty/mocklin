package org.mocklin

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.isA
import org.junit.Assert.assertThat
import org.junit.Test

interface Example {
    fun firstMethod(arg1: String, arg2: Boolean)
    fun secondMethod(arg1: String, arg2: Boolean)
}

class ExpectationTest {

    @Test
    fun matches_shouldBeTrue_whenMethodAndArgsMatch() {
        val rightMethod = Example::class.java.methods[0]

        val expectation = Expectation(ProxyCall(rightMethod, listOf("first-arg", true)))
        val proxyCall = ProxyCall(rightMethod, listOf("first-arg", true))

        assertThat(expectation.matchesProxyCall(proxyCall), equalTo(true))
    }

    @Test
    fun matches_shouldBeFalse_whenMethodMatchesAndArgsDont() {
        val rightMethod = Example::class.java.methods[0]

        val expectation = Expectation(ProxyCall(rightMethod, listOf("first-arg", true)))
        val proxyCall = ProxyCall(rightMethod, listOf("mismatch-arg", true))

        assertThat(expectation.matchesProxyCall(proxyCall), equalTo(false))
    }

    @Test
    fun matches_shouldBeFalse_whenMethodDoesntMatchAndArgsDo() {
        val rightMethod = Example::class.java.methods[0]
        val wrongMethod = Example::class.java.methods[1]

        val expectation = Expectation(ProxyCall(rightMethod, listOf("first-arg", true)))
        val proxyCall = ProxyCall(wrongMethod, listOf("first-arg", true))

        assertThat(expectation.matchesProxyCall(proxyCall), equalTo(false))
    }

    @Test
    fun matches_shouldAllowNoArgMethods_success() {
        val rightMethod = Example::class.java.methods[0]

        val expectation = Expectation(ProxyCall(rightMethod, null))
        val proxyCall = ProxyCall(rightMethod, null)
        assertThat(expectation.matchesProxyCall(proxyCall), equalTo(true))
    }

    @Test
    fun matches_shouldAllowNoArgMethods_failure() {
        val rightMethod = Example::class.java.methods[0]

        val expectation = Expectation(ProxyCall(rightMethod, null))
        val proxyCall = ProxyCall(rightMethod, listOf("first-arg", true))
        assertThat(expectation.matchesProxyCall(proxyCall), equalTo(false))
    }

    @Test
    fun matches_shouldAllowMatchesWithHamcrest_success() {
        val rightMethod = Example::class.java.methods[0]

        val expectation = Expectation(ProxyCall(rightMethod, listOf("", false)))
        expectation.matchers.add(isA(String::class.java))
        expectation.matchers.add(equalTo(true))

        val proxyCall = ProxyCall(rightMethod, listOf("first-arg", true))
        assertThat(expectation.matchesProxyCall(proxyCall), equalTo(true))
    }

    @Test
    fun matches_shouldAllowMatchesWithHamcrest_failure() {
        val rightMethod = Example::class.java.methods[0]

        val expectation = Expectation(ProxyCall(rightMethod, listOf("", false)))
        expectation.matchers.add(isA(Boolean::class.java))
        expectation.matchers.add(equalTo(false))

        val proxyCall = ProxyCall(rightMethod, listOf("first-arg", true))
        assertThat(expectation.matchesProxyCall(proxyCall), equalTo(false))
    }
}