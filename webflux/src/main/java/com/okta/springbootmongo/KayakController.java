package com.okta.springbootmongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping(path = "/kayaks")
public class KayakController {

  @Autowired
  private KayakRepository kayakRepository;

  @PostMapping()
  public @ResponseBody
  Mono<Kayak> addKayak(@RequestBody Kayak kayak) {
    return kayakRepository.save(kayak);
  }

  @GetMapping()
  public @ResponseBody
  Flux<Kayak> getAllKayaks() {
    Flux<Kayak> result = kayakRepository.findAll();
    return result;
  }

}