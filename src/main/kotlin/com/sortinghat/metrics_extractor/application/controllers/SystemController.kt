package com.sortinghat.metrics_extractor.application.controllers

import com.sortinghat.metrics_extractor.application.dto.SystemDto
import com.sortinghat.metrics_extractor.application.services.GetAllSystems
import com.sortinghat.metrics_extractor.application.services.GetSystemData
import com.sortinghat.metrics_extractor.application.services.GetSystemMetrics
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/systems")
class SystemController(
    private val getAllSystems: GetAllSystems,
    private val getSystemData: GetSystemData,
    private val getSystemMetrics: GetSystemMetrics,
) {

    @GetMapping
    fun getAll() = getAllSystems.execute().map { SystemDto.create(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String) = SystemDto.create(getSystemData.execute(id))

    @GetMapping("/{id}/metrics")
    fun getMetrics(@PathVariable id: String) = getSystemMetrics.execute(id)
}
