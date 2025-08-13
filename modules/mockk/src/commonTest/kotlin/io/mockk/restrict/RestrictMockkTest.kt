package io.mockk.restrict

import io.mockk.MockKException
import io.mockk.impl.restrict.MockkValidator
import io.mockk.impl.restrict.RestrictMockkConfiguration
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RestrictMockkTest {
    @Test
    fun `when throwExceptionOnBadMock as true should be throw MockException`() {
        val config = mapOf("mockk.throwExceptionOnBadMock" to "true")

        assertThrows<MockKException> {
            mockk<System>(
                mockValidator = MockkValidator(
                    RestrictMockkConfiguration(TestPropertiesLoader(config))
                )
            )
        }

        assertThrows<MockKException> {
            mockk<File>(
                mockValidator = MockkValidator(
                    RestrictMockkConfiguration(TestPropertiesLoader(config))
                )
            )
        }

        assertThrows<MockKException> {
            mockk<Path>(
                mockValidator = MockkValidator(
                    RestrictMockkConfiguration(TestPropertiesLoader(config))
                )
            )
        }
    }

    @Test
    fun `when throwExceptionOnBadMock as false should not throw exception`() {
        val config = mapOf("mockk.throwExceptionOnBadMock" to "false")

        mockk<File>(
            mockValidator = MockkValidator(
                RestrictMockkConfiguration(TestPropertiesLoader(config))
            )
        )
    }

    @Test
    fun `when throwExceptionOnBadMock is true should throw MockException for collections`() {
        val config = mapOf(
            "mockk.throwExceptionOnBadMock" to "true",
            "mockk.restrictedClasses" to "java.util.Collection, java.util.Map"
        )

        val testValidator = MockkValidator(
            RestrictMockkConfiguration(TestPropertiesLoader(config))
        )

        assertThrows<MockKException> { mockk<HashMap<String, String>>(mockValidator = testValidator) }
        assertThrows<MockKException> { mockk<ArrayList<Int>>(mockValidator = testValidator) }
        assertThrows<MockKException> { mockk<LinkedList<Double>>(mockValidator = testValidator) }
    }

    @Test
    fun `when throwExceptionOnBadMock is false should not throw exception for collections`() {
        val config = mapOf(
            "mockk.throwExceptionOnBadMock" to "false",
        )

        val testValidator = MockkValidator(
            RestrictMockkConfiguration(TestPropertiesLoader(config))
        )

        mockk<HashMap<String, String>>(mockValidator = testValidator)
        mockk<ArrayList<Int>>(mockValidator = testValidator)
        mockk<LinkedList<Double>>(mockValidator = testValidator)
    }

    @Test
    fun `when restricted class does not include collections should not throw exception`() {
        val config = mapOf(
            "mockk.throwExceptionOnBadMock" to "false"
        )

        val testValidator = MockkValidator(
            RestrictMockkConfiguration(TestPropertiesLoader(config))
        )

        mockk<HashMap<String, String>>(mockValidator = testValidator)
        mockk<ArrayList<Int>>(mockValidator = testValidator)
    }

    @Test
    fun `when add custom restricted class should throw exception`() {
        val config = mapOf(
            "mockk.throwExceptionOnBadMock" to "true",
            "mockk.restrictedClasses" to "io.mockk.restrict.Foo"
        )

        val testValidator = MockkValidator(
            RestrictMockkConfiguration(TestPropertiesLoader(config))
        )

        assertThrows<MockKException> {
            mockk<Foo>(mockValidator = testValidator)
        }
    }

    @Test
    fun `when add custom restricted classes should throw exception`() {
        val config = mapOf(
            "mockk.throwExceptionOnBadMock" to "true",
            "mockk.restrictedClasses" to "io.mockk.restrict.Foo, io.mockk.restrict.Bar"
        )

        val testValidator = MockkValidator(
            RestrictMockkConfiguration(TestPropertiesLoader(config))
        )

        assertThrows<MockKException> {
            mockk<Foo>(mockValidator = testValidator)
        }

        assertThrows<MockKException> {
            mockk<Bar>(mockValidator = testValidator)
        }
    }

    @Test
    fun `when add custom restricted sub-class should throw exception`() {
        val config = mapOf(
            "mockk.throwExceptionOnBadMock" to "true",
            "mockk.restrictedClasses" to "io.mockk.restrict.Foo"
        )

        val testValidator = MockkValidator(
            RestrictMockkConfiguration(TestPropertiesLoader(config))
        )

        assertThrows<MockKException> {
            mockk<FooChild>(mockValidator = testValidator)
        }

        assertDoesNotThrow {
            mockk<Bar>(mockValidator = testValidator)
        }
    }
}

open class Foo
class Bar
class FooChild : Foo()