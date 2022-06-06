package com.sortinghat.metrics_extractor.application.controllers

import com.sortinghat.metrics_extractor.application.dto.SystemDto
import com.sortinghat.metrics_extractor.application.services.GetSystemData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/systems")
class SystemController(private val getSystemData: GetSystemData) {

    @GetMapping
    fun getAll() = SystemDto.createFromSystems(getSystemData.findAllSystems())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String) = SystemDto.createFromServices(getSystemData.findAllServicesBySystem(id))

    @GetMapping("/{id}/metrics")
    fun getMetrics(@PathVariable id: String) = getSystemData.getMetricsBySystem(id)
}
