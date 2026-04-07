package com.banka1.stock_service.dto;

/**
 * Response returned after importing FX pair reference data from a CSV source.
 *
 * <p>This mirrors the futures-contract import summary so startup logs and future admin tooling
 * can clearly distinguish how many rows were created, updated, or skipped as unchanged.
 *
 * @param source CSV source location or label
 * @param processedRows number of parsed CSV rows
 * @param createdCount number of newly inserted FX pairs
 * @param updatedCount number of existing FX pairs updated from CSV
 * @param unchangedCount number of existing FX pairs already matching the CSV data
 */
public record ForexPairImportResponse(
        String source,
        int processedRows,
        int createdCount,
        int updatedCount,
        int unchangedCount
) {
}
