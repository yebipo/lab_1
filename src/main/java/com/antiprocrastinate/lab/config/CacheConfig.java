package com.antiprocrastinate.lab.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(
        "tasks_page", "task_item",
        "users_page", "user_item",
        "categories_page", "category_item",
        "skills_page", "skill_item",
        "worklogs_page", "worklog_item"
    );
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(1000)
        .recordStats());
    return cacheManager;
  }
}