package org.mocklin

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Test
import org.mocklin.Mocklin.Companion.dummy


class DummyTest {


    @Test
    fun shouldErrorWhenUsed() {
        val testFunction = { subject: TestSubject -> subject.doAThing() }

        try {
            testFunction(dummy())
            fail("Expected exception")
        } catch (ex: DummyInteractionException) {
            assertThat(ex.message, equalTo("Interaction with method: doAThing. Use a stub instead"))
        }
    }

}

