from datetime import datetime
from typing import List, Optional, Dict
from enum import Enum
from dataclasses import dataclass
from decimal import Decimal

class WorkOrderStatus(Enum):
    PLANNED = "PLANNED"
    IN_PROGRESS = "IN_PROGRESS"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"

class ResourceType(Enum):
    MACHINE = "MACHINE"
    HUMAN = "HUMAN"
    TOOL = "TOOL"

@dataclass
class Material:
    id: str
    name: str
    description: str
    unit_cost: Decimal
    stock_quantity: int
    reorder_point: int
    lead_time_days: int

@dataclass
class BillOfMaterials:
    id: str
    product_id: str
    version: str
    components: Dict[Material, int]  # Material to quantity mapping
    labor_hours: Decimal
    notes: str

@dataclass
class Resource:
    id: str
    name: str
    type: ResourceType
    capacity_per_hour: Decimal
    cost_per_hour: Decimal
    availability_schedule: Dict[datetime, bool]

@dataclass
class WorkOrder:
    id: str
    bom_id: str
    status: WorkOrderStatus
    quantity: int
    start_date: datetime
    end_date: datetime
    assigned_resources: List[Resource]
    actual_labor_hours: Decimal
    actual_material_usage: Dict[Material, int]

@dataclass
class WorkflowStep:
    id: str
    name: str
    description: str
    estimated_duration: int  # in minutes
    required_resources: List[Resource]
    predecessor_steps: List[str]  # List of step IDs

@dataclass
class Workflow:
    id: str
    name: str
    steps: List[WorkflowStep]
    total_estimated_duration: int

@dataclass
class Project:
    id: str
    name: str
    description: str
    start_date: datetime
    end_date: datetime
    work_orders: List[WorkOrder]
    assigned_workflow: Workflow
    budget: Decimal
    actual_cost: Decimal

@dataclass
class TimeEntry:
    id: str
    resource_id: str
    project_id: str
    work_order_id: Optional[str]
    start_time: datetime
    end_time: datetime
    activity_description: str

@dataclass
class ExpenseEntry:
    id: str
    project_id: str
    amount: Decimal
    description: str
    date: datetime
    category: str