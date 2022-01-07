package ua.kpi.iasa.scback.controller;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import ua.kpi.iasa.sc.resoursesgrpc.*;
import ua.kpi.iasa.sc.resoursesgrpc.ResourceGRPCServiceGrpc.ResourceGRPCServiceImplBase;
import ua.kpi.iasa.scback.repository.model.Account;
import ua.kpi.iasa.scback.service.ResourceService;

import java.util.List;
import java.util.stream.IntStream;

public class ResoursesGRPCController extends ResourceGRPCServiceImplBase {
    private ResourceService resourceService;

    public ResoursesGRPCController(ResourceService resourceService){
        this.resourceService = resourceService;
    }

    @Override
    public void getAll(ResourceGRPCRequestMulti request, StreamObserver<ResourceGRPCResponseMulti> responseObserver) {
        List<ua.kpi.iasa.scback.repository.model.Resource> allResources = resourceService.fetchAll();
        ResourceGRPCResponseMulti.Builder resourcesBuilder = ResourceGRPCResponseMulti.newBuilder();
        IntStream.range(0, allResources.size()).forEach(id ->
        {
            ua.kpi.iasa.scback.repository.model.Resource resource = allResources.get(id);
            Account author = resource.getCreatedBy();
            String authorFullname = author.getSurname() + " " + author.getFirstname() + (author.getPatronymic()==null?"":(" " + author.getPatronymic()));
            ResourceBack responseObj = ResourceBack.newBuilder()
                    .setId(resource.getId())
                    .setAdditionalInfo(resource.getAdditionalInfo())
                    .setCreatedAt(Timestamp.newBuilder()
                            .setNanos(resource.getCreatedAt().getNanos())
                            .setSeconds(resource.getCreatedAt().getTime()/1000)
                            .build())
                    .setCreatedBy(authorFullname)
                    .setLink(resource.getLink())
                    .setDiscipline(resource.getDiscipline())
                    .setOutdated(resource.isOutdated())
                    .setTeacher(resource.getTeacher())
                    .build();
            resourcesBuilder.addResources(responseObj);
        }
        );

        ResourceGRPCResponseMulti response = resourcesBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getById(ResourceGRPCRequest request, StreamObserver<ResourceBack> responseObserver) {
        try {
            ua.kpi.iasa.scback.repository.model.Resource res = resourceService.fetchById(request.getId());
            Account author = res.getCreatedBy();
            String authorFullname = author.getSurname() + " " + author.getFirstname() + (author.getPatronymic() == null ? "" : (" " + author.getPatronymic()));
            ResourceBack responseObj = ResourceBack.newBuilder()
                    .setId(res.getId())
                    .setAdditionalInfo(res.getAdditionalInfo())
                    .setCreatedAt(Timestamp.newBuilder()
                            .setNanos(res.getCreatedAt().getNanos())
                            .setSeconds(res.getCreatedAt().getTime()/1000)
                            .build())                    .setCreatedBy(authorFullname)
                    .setLink(res.getLink())
                    .setDiscipline(res.getDiscipline())
                    .setOutdated(res.isOutdated())
                    .setTeacher(res.getTeacher())
                    .build();

            responseObserver.onNext(responseObj);
            responseObserver.onCompleted();
        }
        catch (Exception e){
            responseObserver.onError(e);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void createResource(Resource request, StreamObserver<CreatedResponse> responseObserver) {
        try {
            long id = resourceService.create(request.getAuthorId(),
                request.getTeacher(),
                request.getDiscipline(),
                request.getAdditionalInfo(),
                request.getLink());

            CreatedResponse response = CreatedResponse.newBuilder()
                    .setId(id)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (Exception e){
            responseObserver.onError(e);
            responseObserver.onCompleted();
        }
    }
}
