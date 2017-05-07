package org.mocklin

import org.hamcrest.Matcher


class Expectation(val expected: ProxyCall) {

    var response: Any? = null
    val matchers = mutableListOf<Matcher<out Any>>()

    fun matchesProxyCall(actual: ProxyCall): Boolean {
        val (expectedMethod, expectedArguments) = expected
        val (actualMethod, actualArguments) = actual

        if (expectedMethod != actualMethod) {
            return false
        }

        if (matchers.isEmpty()){
            return expectedArguments == actualArguments
        }

        return actualArguments!!.mapIndexed { index, arg ->
            matchers[index].matches(arg)
        }.reduce { acc, matched -> acc && matched }
    }

}