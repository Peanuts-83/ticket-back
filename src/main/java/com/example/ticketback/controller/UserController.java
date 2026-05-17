package com.example.ticketback.controller;

import com.example.ticketback.domain.enums.UserRole;
import com.example.ticketback.dto.common.BaseHttpParams;
import com.example.ticketback.dto.common.HttpPostResult;
import com.example.ticketback.dto.user.UserDto;
import com.example.ticketback.dto.user.UserFormDto;
import com.example.ticketback.dto.user.UserListDto;
import com.example.ticketback.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(
        name = "Utilisateurs",
        description = "Endpoints de gestion des utilisateurs."
)
public class UserController {
    private final UserService userService;

    @GetMapping("/get/{id}")
    @Operation(
            summary = "[AUTHENTICATED] Récupère un utilisateur par id",
            tags = {"AUTHENTICATED"},
            description = "Retourne le détail d'un utilisateur."
    )
    public HttpPostResult<UserDto> get(@PathVariable Long id) {
        return HttpPostResult.of(userService.get(id));
    }

    @PostMapping("/getList")
    @Operation(
            summary = "[ADMIN] Liste des utilisateurs",
            tags = {"ADMIN"},
            description = """
                     Retourne une liste paginée d'utilisateurs.
                    
                    La pagination est portée par params.paramList :
                    - pageNum : numéro de page.
                    - nb : nombre d'éléments par page.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "getAll",
                                            summary = "Liste complète",
                                            value = "{}"
                                    ),
                                    @ExampleObject(
                                            name = "getOne",
                                            summary = "Un seul élément",
                                            value = """
                                                    {
                                                        "paramList": {
                                                            "pageNum": 1,
                                                            "nb": 1
                                                        }
                                                    }
                                                    """
                                    ),
                            }
                    )
            )
    )
    public HttpPostResult<List<UserListDto>> getList(@RequestBody(required = false) BaseHttpParams params) {
        List<UserListDto> users = userService.getList(params);
        return HttpPostResult.ofList(users, (long) users.size());
    }

    @GetMapping("/getUpdate/{id}")
    @Operation(
            summary = "[OWNER, ADMIN] Détails d'un utilisateur",
            tags = {"OWNER", "ADMIN"},
            description = "Retourne les données d'un utilisateur pour l'écran d'update."
    )
    public HttpPostResult<UserDto> getUpdate(@PathVariable Long id) {
        return HttpPostResult.ofMeta(userService.get(id));
    }

    @PostMapping("/update")
    @Operation(
            summary = "[OWNER, ADMIN] Modification d'un utilisateur",
            tags = {"OWNER", "ADMIN"},
            description = """
                    Modifie un utilisateur.
                    
                    Important :
                    - l'id ne doit pas être dans l'URL ;
                    - l'id métier doit être porté par payload.data.id.
                    """
    )
    public HttpPostResult<UserDto> update(@RequestBody UserFormDto payload) {
        return HttpPostResult.of(userService.update(payload));
    }

    @GetMapping("/metaCreate")
    @SecurityRequirements
    @Operation(
            summary = "[PUBLIC] Métadonnées de création",
            tags = {"PUBLIC"},
            description = """
                    Retourne les métadonnées nécessaires à l'écran de création utilisateur.
                    
                    Endpoint public actuellement prévu dans la configuration de sécurité.
                    """
    )
    public HttpPostResult<UserFormDto> metaCreate() {
        return HttpPostResult.ofMeta(userService.getMetaCreate());
    }

    @PostMapping("/create")
    @SecurityRequirements
    @Operation(
            summary = "[PUBLIC] Création d'un utilisateur",
            tags = {"PUBLIC"},
            description = """
                    Crée un utilisateur.
                    
                    Important :
                    - pas d'id dans l'URL ;
                    - les données sont portées par payload.data.
                    """
    )
    public HttpPostResult<UserDto> create(@RequestBody UserFormDto payload) {
        return HttpPostResult.of(userService.create(payload));
    }

    @PostMapping("/delete")
    @Operation(
            summary = "[OWNER, ADMIN] Suppression d'un utilisateur",
            tags = {"OWNER", "ADMIN"},
            description = "Supprime un utilisateur avec son id, fournie dans payload.data.id."
    )
    public HttpPostResult<Long> delete(@RequestBody Long id) {
        return HttpPostResult.of(userService.delete(id));
    }
}
