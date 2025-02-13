import uuid
from datetime import datetime, timedelta
from decimal import Decimal
from typing import List, Optional, Dict

from src.core_domain_models import TimeEntry, BillOfMaterials, Resource, Workflow, Project, WorkOrder, WorkOrderStatus
from src.core_services import ProductionPlanningService, InventoryManagementService, WorkflowManagementService, \
    ProjectManagementService, TimeAndExpenseService


class ERPSystem:
    def __init__(self):
        self.production_planning = ProductionPlanningService()
        self.inventory_management = InventoryManagementService()
        self.workflow_management = WorkflowManagementService()
        self.project_management = ProjectManagementService()
        self.time_and_expense = TimeAndExpenseService()

    def create_work_order(self, bom_id: str, quantity: int, start_date: datetime) -> Optional[WorkOrder]:
        """
        Create and schedule a new work order.
        """
        # Get BOM and check material availability
        bom = self._get_bom(bom_id)
        if not self.inventory_management.check_material_availability(bom, quantity):
            return None

        # Create work order
        work_order = WorkOrder(
            id=str(uuid.uuid4()),
            bom_id=bom_id,
            status=WorkOrderStatus.PLANNED,
            quantity=quantity,
            start_date=start_date,
            end_date=self._calculate_end_date(bom, quantity, start_date),
            assigned_resources=[],
            actual_labor_hours=Decimal('0'),
            actual_material_usage={}
        )

        # Schedule resources
        available_resources = self._get_available_resources(work_order.start_date, work_order.end_date)
        if not self.production_planning.schedule_work_order(work_order, available_resources):
            return None

        # Reserve materials
        self.inventory_management.reserve_materials(bom, quantity)

        return work_order

    def create_project(self, name: str, description: str, start_date: datetime, workflow_template_id: str) -> Project:
        """
        Create a new project with associated workflow.
        """
        workflow_template = self._get_workflow_template(workflow_template_id)

        project = Project(
            id=str(uuid.uuid4()),
            name=name,
            description=description,
            start_date=start_date,
            end_date=start_date + timedelta(days=workflow_template.total_estimated_duration),
            work_orders=[],
            assigned_workflow=None,
            budget=Decimal('0'),
            actual_cost=Decimal('0')
        )

        self.workflow_management.create_workflow_instance(workflow_template, project)
        return project

    def update_project_progress(self, project_id: str) -> Dict:
        """
        Update and return project metrics.
        """
        project = self._get_project(project_id)
        return self.project_management.calculate_project_metrics(project)

    def log_time(self, resource_id: str, project_id: str, work_order_id: str,
                 start_time: datetime, end_time: datetime, description: str) -> None:
        """
        Log time entry for a resource.
        """
        entry = TimeEntry(
            id=str(uuid.uuid4()),
            resource_id=resource_id,
            project_id=project_id,
            work_order_id=work_order_id,
            start_time=start_time,
            end_time=end_time,
            activity_description=description
        )
        self.time_and_expense.log_time_entry(entry)

    # Helper methods would be implemented here
    def _get_bom(self, bom_id: str) -> BillOfMaterials:
        pass

    def _calculate_end_date(self, bom: BillOfMaterials, quantity: int, start_date: datetime) -> datetime:
        pass

    def _get_available_resources(self, start_date: datetime, end_date: datetime) -> List[Resource]:
        pass

    def _get_workflow_template(self, workflow_id: str) -> Workflow:
        pass

    def _get_project(self, project_id: str) -> Project:
        pass