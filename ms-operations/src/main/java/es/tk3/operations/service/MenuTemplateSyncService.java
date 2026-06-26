package es.tk3.operations.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.operations.dto.MenuTemplateSyncEventDTO;
import es.tk3.operations.model.MenuTemplateRef;
import es.tk3.operations.repository.MenuTemplateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;


@Service
public class MenuTemplateSyncService {
    private static final Logger logger = Logger.getLogger(MenuTemplateSyncService.class.getName());

    private final MenuTemplateRepository menuTemplateRepository;
    private final ObjectMapper mapper;

    public MenuTemplateSyncService(
            MenuTemplateRepository menuTemplateRepository,
            ObjectMapper mapper)
    {
        this.menuTemplateRepository = menuTemplateRepository;
        this.mapper = mapper;
    }

    @Transactional
    public void syncTemplateReference(MenuTemplateSyncEventDTO event) {
        try {
            if (event.getTemplateId() == null) {
                throw new IllegalArgumentException("El ID de la plantilla no puede ser nulo");
            }

            MenuTemplateRef template = menuTemplateRepository.findById(event.getTemplateId())
                    .orElse(new MenuTemplateRef());

            if (template.getId() == null) {
                template.setId(event.getTemplateId());
            }

            template.setName(event.getName());
            String jsonStructure = mapper.writeValueAsString(event);
            template.setCategoryStructureJson(jsonStructure);
            menuTemplateRepository.save(template);

            logger.info("Sincronización exitosa para Template ID: " + event.getTemplateId());

        } catch (Exception e) {
            throw new RuntimeException("Error crítico en la sincronización: " + e.getMessage(), e);
        }
    }
}
