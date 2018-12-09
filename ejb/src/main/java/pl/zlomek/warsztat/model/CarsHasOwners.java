package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cars_has_owners")
@Getter
@Setter
@NoArgsConstructor
public class CarsHasOwners implements Serializable {

    @EmbeddedId
    private CarHasOwnerId id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Client owner;

    @NotNull
    private OwnershipStatus status;

    @NotNull
    @Column(name = "begin_ownership_date")
    private LocalDate beginOwnershipDate;

    @Column(name = "end_ownership_date")
    private LocalDate endOwnershipDate;

    @NotNull
    @Size(min = 7, max = 8)
    @Column(name = "registration_number")
    private String registrationNumber;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    private static class CarHasOwnerId implements Serializable{

        @NotNull
        @Column(name = "car_pk")
        private long carId;

        @NotNull
        @Column(name = "owner_pk")
        private long ownerId;

        @Override
        public boolean equals(Object object){
            if (this == object) return true;

            if (object == null || getClass() != object.getClass())
                return false;

            CarHasOwnerId that = (CarHasOwnerId) object;
            return Objects.equals(carId, that.carId) &&
                    Objects.equals(ownerId, that.ownerId);
        }

        @Override
        public int hashCode(){
            return Objects.hash(carId, ownerId);
        }

        public CarHasOwnerId(Car car, Client client){
            this.carId = car.getId();
            this.ownerId = client.getClientId();
        }
    }

    public CarsHasOwners(Car car, Client client, OwnershipStatus status, String registrationNumber) {
        this.id = new CarHasOwnerId(car, client);
        this.car = car;
        this.owner = client;
        this.status = status;
        this.beginOwnershipDate = LocalDate.now();
        this.registrationNumber = registrationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if (o.getClass() == Car.class){
            return (o.equals(this.car));
        }
        if(o.getClass() == Client.class){
            return (o).equals(this.owner);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, car, owner, status);
    }
}
