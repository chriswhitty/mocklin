package org.mocklin

import org.hamcrest.Matcher
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method


typealias ProxyCall = Pair<Method, List<Any?>?>

class UnexpectedVerificationException(proxyCall: ProxyCall) :
        RuntimeException("Unexpected mock interaction, method: ${proxyCall.first.name} args: ${proxyCall.second}")

class MockProxy : InvocationHandler {

    private val expectations = mutableListOf<Expectation>()

    override fun invoke(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
        if (method.name == "toString") {
            return "Mock for ${method.declaringClass}"
        }

        val request = ProxyCall(method, args?.toList())

        Mocklin.lastMockInteraction = this
        if (Mocklin.recordingExpectations) {
            expectations.add(Expectation(request))
            return null
        }

        if (!hasExpectectation(request)) {
            throw UnexpectedVerificationException(request)
        }

        val expectation = getMatchingExpectation(request).last()
        return expectation.response
    }

    private fun hasExpectectation(request: ProxyCall) = getMatchingExpectation(request).isNotEmpty()

    private fun getMatchingExpectation(request: ProxyCall) = expectations
            .filter { entry -> entry.matchesProxyCall(request) }

    fun setExpectedResponse(expectedResponse: Any) {
        expectations.last().response = expectedResponse
    }

    fun setExpectedArgumentMatchers(matchers: List<Matcher<out Any>>) {
        expectations.last().matchers.addAll(matchers)
    }
}
