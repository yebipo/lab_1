package com.antiprocrastinate.lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Schema(description = "Ответ со списком и пагинацией")
public class PageResponse<T> {
  @Schema(description = "Список элементов на текущей странице")
  private List<T> content;

  @Schema(description = "Общее количество страниц")
  private int totalPages;

  @Schema(description = "Общее количество элементов")
  private long totalElements;

  @Schema(description = "Размер страницы")
  private int size;

  @Schema(description = "Номер текущей страницы (с нуля)")
  private int number;

  public static <T> PageResponse<T> of(Page<T> page) {
    PageResponse<T> response = new PageResponse<>();
    response.setContent(page.getContent());
    response.setTotalPages(page.getTotalPages());
    response.setTotalElements(page.getTotalElements());
    response.setSize(page.getSize());
    response.setNumber(page.getNumber());
    return response;
  }
}