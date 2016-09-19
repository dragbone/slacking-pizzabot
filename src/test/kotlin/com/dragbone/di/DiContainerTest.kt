package com.dragbone.di

import com.dragbone.di.DiContainer
import org.junit.Assert.*
import org.junit.Test

class DiContainerTest {
    interface IService
    class Service : IService

    class DependsOnConcrete(val service: Service)

    @Test fun testSetupConcrete() {
        val di = DiContainer()
        di.setupConcrete<Service>()

        val r = di.instantiate<DependsOnConcrete>()
        assertNotNull(r)
        assertNotNull(r.service)
    }

    class DependsOnInterface(val service: IService)

    @Test fun testSetup() {
        val di = DiContainer()
        di.setup<IService, Service>()

        val r = di.instantiate<DependsOnInterface>()
        assertNotNull(r)
        assertNotNull(r.service)
    }

    class Nested(val dep: DependsOnInterface)

    @Test fun testSetupNested() {
        val di = DiContainer()
        di.setup<IService, Service>()
        di.setupConcrete<DependsOnInterface>()

        val r = di.instantiate<Nested>()
        assertNotNull(r)
        assertNotNull(r.dep)
        assertNotNull(r.dep.service)
    }

    class NestedPlus(val dep: DependsOnInterface, val service: IService)

    @Test fun testSetupNestedSingleInstance() {
        val di = DiContainer()
        di.setup<IService, Service>()
        di.setupConcrete<DependsOnInterface>()

        val r = di.instantiate<NestedPlus>()
        assertNotNull(r)
        assertNotNull(r.dep)
        assertNotNull(r.dep.service)
        assertNotNull(r.service)
        assertEquals(r.dep.service, r.service)
    }
}