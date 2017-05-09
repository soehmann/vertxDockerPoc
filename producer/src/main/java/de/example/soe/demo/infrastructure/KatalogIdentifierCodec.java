package de.example.soe.demo.infrastructure;

import org.springframework.stereotype.Component;

import de.example.soe.demo.domain.KatalogIdentifier;

@Component
public class KatalogIdentifierCodec extends AbstractCodec<KatalogIdentifier> {

    @Override
    public Class getEncodedClass() {
        return KatalogIdentifier.class;
    }

}
