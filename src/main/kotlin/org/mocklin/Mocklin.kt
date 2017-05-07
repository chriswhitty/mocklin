package org.mocklin

import org.hamcrest.Matcher
import java.lang.reflect.Proxy


class Mocklin {

    companion object {

        var lastMockInteraction: MockProxy? = null
        var recordingExpectations = false


        inline fun <reified T : Any> stub(): T {
            return Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java), MockProxy()) as T
        }

        fun whenever(func: () -> Unit): OngoingStubbing {
            recordingExpectations = true
            func()
            recordingExpectations = false
            lastMockInteraction!!.setExpectedArgumentMatchers(listOfMatchers)
            listOfMatchers.clear()
            return OngoingStubbing()
        }

        val listOfMatchers = mutableListOf<Matcher<out Any>>()
        inline fun <reified T : Any> match(matcher: Matcher<T>): T {
            listOfMatchers.add(matcher)
            return T::class.java.newInstance()
        }
    }
}

class OngoingStubbing {

    fun thenReturn(expectedResponse: Any) {
        Mocklin.lastMockInteraction?.setExpectedResponse(expectedResponse)
    }

}

