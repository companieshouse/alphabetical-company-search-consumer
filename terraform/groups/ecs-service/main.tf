provider "aws" {
  region = var.aws_region
}

terraform {
  backend "s3" {
  }
  required_version = "~> 1.3"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.54.0"
    }
    vault = {
      source  = "hashicorp/vault"
      version = "~> 3.18.0"
    }
  }
}

module "secrets" {
  source = "git@github.com:companieshouse/terraform-modules//aws/ecs/secrets?ref=1.0.294"

  name_prefix = "${local.service_name}-${var.environment}"
  environment = var.environment
  kms_key_id  = data.aws_kms_key.kms_key.id
  secrets     = local.service_vault_secrets
}

module "ecs-service" {
  source = "git@github.com:companieshouse/terraform-modules//aws/ecs/ecs-service?ref=1.0.294"

  # Environmental configuration
  environment             = var.environment
  aws_region              = var.aws_region
  aws_profile             = var.aws_profile
  vpc_id                  = data.aws_vpc.vpc.id
  ecs_cluster_id          = data.aws_ecs_cluster.ecs_cluster.id
  task_execution_role_arn = data.aws_iam_role.ecs_cluster_iam_role.arn

  # Load balancer configuration
  batch_service = true # no ALB attachment required for consumer app

  # ECS Task container health check
  use_task_container_healthcheck = true
  healthcheck_path               = local.healthcheck_path
  healthcheck_matcher            = local.healthcheck_matcher
  health_check_grace_period_seconds = 240
  healthcheck_healthy_threshold     = "2"
  healthcheck_unhealthy_threshold   = "5"

  # Disable cloudwatch alarms that are dependant on loadbalancer
  cloudwatch_unhealthy_host_count_enabled = false
  cloudwatch_healthy_host_count_enabled   = false
  cloudwatch_response_time_enabled        = false
  cloudwatch_http_5xx_error_count_enabled = false

  # Docker container details
  docker_registry   = var.docker_registry
  docker_repo       = local.docker_repo
  container_version = var.alphabetical_company_search_consumer_version
  container_port    = local.container_port

  # Service configuration
  service_name                       = local.service_name
  name_prefix                        = local.name_prefix
  desired_task_count                 = var.desired_task_count
  max_task_count                     = var.max_task_count
  required_cpus                      = var.required_cpus
  required_memory                    = var.required_memory
  service_autoscale_enabled          = var.service_autoscale_enabled
  service_autoscale_target_value_cpu = var.service_autoscale_target_value_cpu
  service_scaledown_schedule         = var.service_scaledown_schedule
  service_scaleup_schedule           = var.service_scaleup_schedule
  use_capacity_provider              = var.use_capacity_provider
  use_fargate                        = var.use_fargate
  fargate_subnets                    = local.application_subnet_ids

  # Cloudwatch
  cloudwatch_alarms_enabled = var.cloudwatch_alarms_enabled

  # Service environment variable and secret configs
  task_environment          = local.task_environment
  task_secrets              = local.task_secrets
  app_environment_filename  = local.app_environment_filename
  use_set_environment_files = local.use_set_environment_files
}