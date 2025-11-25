package com.anborja.tucarro.infrastructure.driving.http.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private PageInfo pageInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int page;
        private int size;
        private int totalPages;
        private long totalElements;
        private boolean first;
        private boolean last;
        private boolean hasNext;
        private boolean hasPrevious;
        private SortInfo sort;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortInfo {
        private boolean sorted;
        private String sortBy;
        private String direction;
    }

    /**
     * Convierte un Page de Spring Data a PagedResponse
     */
    public static <T> PagedResponse<T> fromPage(Page<T> page) {
        SortInfo sortInfo = SortInfo.builder()
                .sorted(page.getSort().isSorted())
                .sortBy(page.getSort().isSorted() ?
                        page.getSort().iterator().next().getProperty() : null)
                .direction(page.getSort().isSorted() ?
                        page.getSort().iterator().next().getDirection().name() : null)
                .build();

        PageInfo pageInfo = PageInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .sort(sortInfo)
                .build();

        return PagedResponse.<T>builder()
                .content(page.getContent())
                .pageInfo(pageInfo)
                .build();
    }
}