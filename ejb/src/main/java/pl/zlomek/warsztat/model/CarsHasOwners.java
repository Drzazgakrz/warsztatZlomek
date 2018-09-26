package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
    private Car car;

    @ManyToOne
    private Client owner;

    @NotNull
    private OwnershipStatus status;


    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    private static class CarHasOwnerId implements Serializable{

        @NotNull
        private long carId;

        @NotNull
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

    public CarsHasOwners(Car car, Client client, OwnershipStatus status) {
        this.id = new CarHasOwnerId(car, client);
        this.car = car;
        this.owner = client;
        this.status = status;
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
