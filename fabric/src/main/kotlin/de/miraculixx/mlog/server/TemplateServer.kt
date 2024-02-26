package de.miraculixx.mlog.server

import net.fabricmc.api.DedicatedServerModInitializer

class TemplateServer: DedicatedServerModInitializer {

    override fun onInitializeServer() {
        println("Hello server!")
    }

}