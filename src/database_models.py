from sqlalchemy import create_engine, Column, Integer, String, Float, DateTime, ForeignKey, Enum as SQLEnum, Numeric
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship, sessionmaker
from datetime import datetime
from typing import List
import enum

Base = declarative_base()

class UserRole(enum.Enum):
    ADMIN = "admin"
    MANAGER = "manager"
    WORKER = "worker"

class User(Base):
    __tablename__ = 'users'

    id = Column(Integer, primary_key=True)
    username = Column(String(50), unique=True, nullable=False)
    email = Column(String(100), unique=True, nullable=False)
    password_hash = Column(String(200), nullable=False)
    role = Column(SQLEnum(UserRole), nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    # Relationships
    time_entries = relationship("TimeEntry", back_populates="user")

class Material(Base):
    __tablename__ = 'materials'

    id = Column(Integer, primary_key=True)
    name = Column(String(100), nullable=False)
    description = Column(String(500))
    unit_cost = Column(Numeric(10, 2), nullable=False)
    stock_quantity = Column(Integer, default=0)
    reorder_point = Column(Integer, default=0)
    lead_time_days = Column(Integer, default=0)

    # Relationships
    bom_entries = relationship("BOMEntry", back_populates="material")

class BillOfMaterials(Base):
    __tablename__ = 'bill_of_materials'

    id = Column(Integer, primary_key=True)
    product_id = Column(String(50), nullable=False)
    version = Column(String(20), nullable=False)
    labor_hours = Column(Numeric(10, 2), nullable=False)
    notes = Column(String(1000))

    # Relationships
    components = relationship("BOMEntry", back_populates="bom")
    work_orders = relationship("WorkOrder", back_populates="bom")

class BOMEntry(Base):
    __tablename__ = 'bom_entries'

    id = Column(Integer, primary_key=True)
    bom_id = Column(Integer, ForeignKey('bill_of_materials.id'))
    material_id = Column(Integer, ForeignKey('materials.id'))
    quantity = Column(Integer, nullable=False)

    # Relationships
    bom = relationship("BillOfMaterials", back_populates="components")
    material = relationship("Material", back_populates="bom_entries")

class WorkOrder(Base):
    __tablename__ = 'work_orders'

    id = Column(Integer, primary_key=True)
    bom_id = Column(Integer, ForeignKey('bill_of_materials.id'))
    project_id = Column(Integer, ForeignKey('projects.id'))
    status = Column(String(20), nullable=False)
    quantity = Column(Integer, nullable=False)
    start_date = Column(DateTime, nullable=False)
    end_date = Column(DateTime, nullable=False)
    actual_labor_hours = Column(Numeric(10, 2), default=0)

    # Relationships
    bom = relationship("BillOfMaterials", back_populates="work_orders")
    project = relationship("Project", back_populates="work_orders")
    resource_assignments = relationship("ResourceAssignment", back_populates="work_order")
    material_usage = relationship("MaterialUsage", back_populates="work_order")

class Project(Base):
    __tablename__ = 'projects'

    id = Column(Integer, primary_key=True)
    name = Column(String(100), nullable=False)
    description = Column(String(500))
    start_date = Column(DateTime, nullable=False)
    end_date = Column(DateTime, nullable=False)
    budget = Column(Numeric(12, 2), nullable=False)
    actual_cost = Column(Numeric(12, 2), default=0)
    workflow_id = Column(Integer, ForeignKey('workflows.id'))

    # Relationships
    work_orders = relationship("WorkOrder", back_populates="project")
    workflow = relationship("Workflow", back_populates="projects")
    time_entries = relationship("TimeEntry", back_populates="project")
    expense_entries = relationship("ExpenseEntry", back_populates="project")

class ResourceAssignment(Base):
    __tablename__ = 'resource_assignments'

    id = Column(Integer, primary_key=True)
    work_order_id = Column(Integer, ForeignKey('work_orders.id'))
    resource_id = Column(Integer, ForeignKey('resources.id'))
    start_time = Column(DateTime, nullable=False)
    end_time = Column(DateTime, nullable=False)

    # Relationships
    work_order = relationship("WorkOrder", back_populates="resource_assignments")
    resource = relationship("Resource", back_populates="assignments")

class MaterialUsage(Base):
    __tablename__ = 'material_usage'

    id = Column(Integer, primary_key=True)
    work_order_id = Column(Integer, ForeignKey('work_orders.id'))
    material_id = Column(Integer, ForeignKey('materials.id'))
    quantity_used = Column(Integer, nullable=False)

    # Relationships
    work_order = relationship("WorkOrder", back_populates="material_usage")
    material = relationship("Material")

# Database initialization
def init_db():
    engine = create_engine('postgresql://username:password@localhost/erp_db')
    Base.metadata.create_all(engine)
    return engine

# Session management
Session = sessionmaker()