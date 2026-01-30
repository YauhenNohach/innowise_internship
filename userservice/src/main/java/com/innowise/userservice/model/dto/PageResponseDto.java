package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated response object")
public class PageResponseDto<T> {

  @Schema(description = "List of items on current page")
  private List<T> content;

  @Schema(description = "Current page number (starting from 0)", example = "0")
  private Integer pageNumber;

  @Schema(description = "Page size", example = "10")
  private Integer pageSize;

  @Schema(description = "Total number of elements", example = "100")
  private Long totalElements;

  @Schema(description = "Total number of pages", example = "10")
  private Integer totalPages;

  @Schema(description = "First page indicator", example = "true")
  private Boolean first;

  @Schema(description = "Last page indicator", example = "false")
  private Boolean last;

  @Schema(description = "Number of elements on current page", example = "10")
  private Integer numberOfElements;

  @Schema(description = "Empty page indicator", example = "false")
  private Boolean empty;
}
