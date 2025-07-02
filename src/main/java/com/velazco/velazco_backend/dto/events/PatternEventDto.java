package com.velazco.velazco_backend.dto.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatternEventDto<T> {
  private String pattern;
  private T data;
}
