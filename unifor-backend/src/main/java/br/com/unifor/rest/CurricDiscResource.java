package br.com.unifor.rest;

import br.com.unifor.dto.CurricDiscRequestDTO;
import br.com.unifor.dto.CurricDiscResponseDTO;
import br.com.unifor.service.CurricDiscService;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/curriculum/{curriculumId}/disciplines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"COORDENADOR", "ADMIN"})
public class CurricDiscResource {

    // Recurso REST para gerenciamento de disciplinas em matrizes curriculares.
    // Controla as disciplinas que compõem cada matriz curricular.
    //
    // Decisões técnicas:
    // - Endpoint aninhado sob /curriculum/{curriculumId}/disciplines
    // - Relacionamento N:N entre matriz curricular e disciplinas
    // - Campos adicionais: período sugerido, pré-requisitos
    //
    // Permissões:
    // - Todas as operações restritas a COORDENADOR e ADMIN
    //
    // Observações:
    // - IDs são gerados automaticamente como UUID
    // - Autenticação e autorização via Keycloak

    @Inject
    CurricDiscService curricDiscService;

    @GET
    public List<CurricDiscResponseDTO> list(@PathParam("curriculumId") UUID curriculumId) {
        // filtra apenas as disciplinas da matriz curricular informada
        return curricDiscService.listCurricDiscs().stream()
            .filter(dto -> dto.getCurriculumId().equals(curriculumId))
            .toList();
    }

    @GET
    @Path("{disciplineId}")
    public Response get(@PathParam("curriculumId") UUID curriculumId,
                        @PathParam("disciplineId") UUID disciplineId) {
        CurricDiscResponseDTO dto = curricDiscService.getCurricDisc(curriculumId, disciplineId);
        return Response.ok(dto).build();
    }

    @POST
    public Response create(@PathParam("curriculumId") UUID curriculumId,
                           CurricDiscRequestDTO dto,
                           @Context UriInfo uriInfo) {
        dto.setCurriculumId(curriculumId);
        CurricDiscResponseDTO created = curricDiscService.createCurricDisc(dto);
        URI uri = uriInfo.getAbsolutePathBuilder().path(created.getDisciplineId().toString()).build();
        return Response.created(uri).entity(created).build();
    }

    @PUT
    @Path("{disciplineId}")
    public Response update(@PathParam("curriculumId") UUID curriculumId,
                          @PathParam("disciplineId") UUID disciplineId,
                          CurricDiscRequestDTO dto) {
        dto.setCurriculumId(curriculumId);
        dto.setDisciplineId(disciplineId);
        CurricDiscResponseDTO updated = curricDiscService.updateCurricDisc(curriculumId, disciplineId, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{disciplineId}")
    public Response delete(@PathParam("curriculumId") UUID curriculumId,
                          @PathParam("disciplineId") UUID disciplineId) {
        curricDiscService.deleteCurricDisc(curriculumId, disciplineId);
        return Response.noContent().build();
    }
}