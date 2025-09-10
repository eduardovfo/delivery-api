package br.com.delivery.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resposta paginada genérica")
public class PageResponse<T> {
    @Schema(description = "Lista de itens da página atual")
    private List<T> content;
    
    @Schema(description = "Número da página atual (0-based)", example = "0")
    private int page;
    
    @Schema(description = "Tamanho da página", example = "20")
    private int size;
    
    @Schema(description = "Total de elementos em todas as páginas", example = "100")
    private long totalElements;
    
    @Schema(description = "Total de páginas", example = "5")
    private int totalPages;
    
    @Schema(description = "Indica se é a primeira página", example = "true")
    private boolean first;
    
    @Schema(description = "Indica se é a última página", example = "false")
    private boolean last;

    public PageResponse() {}

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
