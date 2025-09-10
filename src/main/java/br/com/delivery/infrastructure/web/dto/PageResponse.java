package br.com.delivery.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Resposta paginada genérica")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
