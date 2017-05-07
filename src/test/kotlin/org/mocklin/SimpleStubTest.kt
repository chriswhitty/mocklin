package org.mocklin

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.isA
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Test
import org.mocklin.Mocklin.Companion.match
import org.mocklin.Mocklin.Companion.stub
import org.mocklin.Mocklin.Companion.whenever


interface TestSubject {
    fun doAThing(): String
    fun doAThingWithArg(arg: String): String
}

class SimpleStubTest {

    @Test
    fun shouldStubAMethod() {
        val expectedResponse = "the-response"
        val stubbedSubject = stub<TestSubject>()

        whenever { stubbedSubject.doAThing() }.thenReturn(expectedResponse)
        assertThat(stubbedSubject.doAThing(), equalTo(expectedResponse))
    }

    @Test
    fun shouldStubForMethodWithArgs() {
        val expectedResponse = "the-response"

        val stubbedSubject = stub<TestSubject>()

        val expectedArg = "expected-arg"
        whenever { stubbedSubject.doAThingWithArg(expectedArg) }.thenReturn(expectedResponse)
        assertThat(stubbedSubject.doAThingWithArg(expectedArg), equalTo(expectedResponse))
    }

    @Test
    fun shouldStubForMethodWithArgsAndMatchers() {
        val expectedResponse = "the-response"

        val stubbedSubject = stub<TestSubject>()

        val expectedArg = "expected-arg"
        whenever { stubbedSubject.doAThingWithArg(match(isA(String::class.java))) }.thenReturn(expectedResponse)
        assertThat(stubbedSubject.doAThingWithArg(expectedArg), equalTo(expectedResponse))
    }

    @Test
    fun shouldNotReturnWhenArgsDoNotMatch() {
        val stubbedSubject = stub<TestSubject>()
        whenever { stubbedSubject.doAThingWithArg("expected-arg") }.thenReturn("the-response")

        try {
            stubbedSubject.doAThingWithArg("mismatch-arg")
            fail("Expected exception")
        } catch(ex: UnexpectedVerificationException) {
            assertThat(ex.message, equalTo("Unexpected mock interaction, method: doAThingWithArg args: [mismatch-arg]"))
        }
    }

    @Test(expected = UnexpectedVerificationException::class)
    fun shouldClearMatcherListBetweenStubs() {
        val stubbedSubject1 = stub<TestSubject>()
        whenever { stubbedSubject1.doAThingWithArg(match(isA(String::class.java))) }.thenReturn("good")

        val stubbedSubject2 = stub<TestSubject>()
        whenever { stubbedSubject2.doAThingWithArg("expected-arg") }.thenReturn("bad")
        stubbedSubject2.doAThingWithArg("mismatch-arg")
    }

    @Test
    fun shouldAllowMultipleExpectations() {
        val stubbedSubject1 = stub<TestSubject>()
        whenever { stubbedSubject1.doAThingWithArg(match(isA(String::class.java))) }.thenReturn("hamcrest-match-expected")
        whenever { stubbedSubject1.doAThingWithArg("explicit-match") }.thenReturn("explicit-match-expected")

        assertThat(stubbedSubject1.doAThingWithArg("hamcrest-match"), equalTo("hamcrest-match-expected"))
        assertThat(stubbedSubject1.doAThingWithArg("explicit-match"), equalTo("explicit-match-expected"))
    }


}
