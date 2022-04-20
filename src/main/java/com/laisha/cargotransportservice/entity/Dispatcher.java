package com.laisha.cargotransportservice.entity;

import java.time.LocalDate;

public class Dispatcher extends User{

    private int maxOrderQuantity;

    public static Builder createBuilder(){
        return new Dispatcher().new Builder();
    }

    public class Builder {

        private Builder(){
        }

        public Builder setLogin(String login) {

            Dispatcher.this.setLogin(login);
            return this;
        }

        public Builder setSurname(String surname) {

            Dispatcher.this.setSurname(surname);
            return this;
        }

        public Builder setFirstName(String firstName) {

            Dispatcher.this.setFirstName(firstName);
            return this;
        }

        public Builder setSecondName(String secondName) {

            Dispatcher.this.setFirstName(secondName);
            return this;
        }

        public Builder setBirthDate(LocalDate birthDate) {

            Dispatcher.this.setBirthDate(birthDate);
            return this;
        }

        public Builder setEmail(String email) {

            Dispatcher.this.setFirstName(email);
            return this;
        }

        public Builder setEmploymentDate(LocalDate employmentDate) {

            Dispatcher.this.setEmploymentDate(employmentDate);
            return this;
        }

        public Builder setRole(UserRole role) {

            Dispatcher.this.setRole(role);
            return this;
        }

        public Builder setStatus(UserStatus status) {

            Dispatcher.this.setStatus(status);
            return this;
        }

        public  Dispatcher build(){
            return Dispatcher.this;
        }
    }

    public int getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    public void setMaxOrderQuantity(int maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    //fixme correct methods below
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dispatcher)) return false;
        if (!super.equals(o)) return false;

        Dispatcher that = (Dispatcher) o;

        return maxOrderQuantity == that.maxOrderQuantity;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + maxOrderQuantity;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Dispatcher{");
        sb.append("maxOrderQuantity=").append(maxOrderQuantity);
        sb.append('}');
        return sb.toString();
    }
}
