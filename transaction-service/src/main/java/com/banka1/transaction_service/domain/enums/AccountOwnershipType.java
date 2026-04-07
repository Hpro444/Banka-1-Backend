package com.banka1.transaction_service.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum koji definiše tipove vlasništva bankarskog računa.
 */
@AllArgsConstructor
@Getter
public enum AccountOwnershipType {

    /** Lični račun - vlasnik je fizičko lice */
    PERSONAL(21),

    /** Poslovni račun - vlasnik je pravno lice/kompanija */
    BUSINESS(22);

    /** Numerička vrednost povezana sa tipom vlasništva */
    private final int val;

}
