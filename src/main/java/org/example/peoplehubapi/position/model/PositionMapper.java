package org.example.peoplehubapi.position.model;

public class PositionMapper {

    public static PositionDTO toDTO(Position position) {
        if (position == null) {
            return null;
        }

        PositionDTO dto = new PositionDTO();
        dto.setId(position.getId());
        dto.setName(position.getName());
        dto.setSalary(position.getSalary());
        dto.setStartDate(position.getStartDate());
        dto.setEndDate(position.getEndDate());
        if (position.getEmployee() != null) {
            dto.setEmployeeId(position.getEmployee().getId());
        }
        return dto;
    }

    public static Position fromCreateCommand(CreatePositionCommand command) {
        Position position = new Position();
        position.setName(command.getName());
        position.setSalary(command.getSalary());
        position.setStartDate(command.getStartDate());
        position.setEndDate(command.getEndDate());
        return position;
    }

}
