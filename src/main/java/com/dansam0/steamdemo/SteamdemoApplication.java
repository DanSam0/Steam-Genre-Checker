package com.dansam0.steamdemo;

import com.dansam0.steamdemo.service.ProfileService;
import com.dansam0.steamdemo.steam.SteamApi;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SteamdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SteamdemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ProfileService profileService, SteamApi steamApi){

		return runner -> {

		};

	}

}
