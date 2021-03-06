package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * @author dteixeira
 */

@DatabaseSetup(value = "InteractionTest.xml", type = DatabaseOperation.INSERT)
public class InteractionDaoTest extends CoreUnitBaseTest {

	@Autowired InteractionDAO interactionDAO;

	@Test
	public void shouldGetListOfInteractions() {

		List<Interaction> interactions = interactionDAO.findInteractionsByEntry("NX_P51813");
		assertEquals(interactions.size(), 5);

	}

}
