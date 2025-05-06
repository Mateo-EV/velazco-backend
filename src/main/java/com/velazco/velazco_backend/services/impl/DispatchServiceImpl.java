package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.repositories.DispatchRepository;
import com.velazco.velazco_backend.services.DispatchService;
import org.springframework.stereotype.Service;

@Service
public class DispatchServiceImpl implements DispatchService {
  @SuppressWarnings("unused")
  private final DispatchRepository dispatchRepository;

  public DispatchServiceImpl(DispatchRepository dispatchRepository) {
    this.dispatchRepository = dispatchRepository;
  }
}