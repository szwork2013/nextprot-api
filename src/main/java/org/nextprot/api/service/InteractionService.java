package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.Interaction;
import org.nextprot.api.service.annotation.ValidEntry;

public interface InteractionService {

	List<Interaction> findInteractionsByEntry(@ValidEntry String entryName);
	List<Interaction> findAllInteractions();
}
